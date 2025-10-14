#!/bin/bash

# === CONFIGURATION ===
JAR_PATH="target/products-0.0.1-SNAPSHOT.jar"
S3_BUCKET="products-lambda-jar"
ROLE_ARN="arn:aws:iam::204620195036:role/LambdaExec"
REGION="ap-south-1"
API_NAME="swift-cart-rest"
STAGE_NAME="swift-cart"
VPC_ID="vpc-0c0a5a82dca767baf"
SUBNET_IDS="subnet-053620b9c1566d4d8,subnet-0ed513ccf15f94bbe,subnet-0e78173bed16975a7"
SECURITY_GROUP_ID="sg-0a36b067f010159dc"
AWS="/c/Program Files/Amazon/AWSCLIV2/aws"

FUNC_NAME="CartControllerFunction"
HANDLER="org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"
MAIN_CLASS="com.swiftcart.products.ProductsApplication"
FUNCTION_DEF="cartController"

ROUTES=(
  "GET|cart"
  "POST|cart"
  "PUT|cart"
  "DELETE|cart"
)

# === STEP 1: Upload JAR to S3 ===
echo "📦 Uploading JAR to S3..."
"$AWS" s3 cp $JAR_PATH s3://$S3_BUCKET/products-service.jar --region $REGION

# === STEP 2: Create REST API Gateway ===
echo "🔍 Checking if REST API already exists..."
API_ID=$("$AWS" apigateway get-rest-apis --region $REGION --query "items[?name=='$API_NAME'].id" --output text)

if [ -z "$API_ID" ]; then
  echo "🆕 Creating new REST API Gateway: $API_NAME"
  API_ID=$("$AWS" apigateway create-rest-api \
    --name "$API_NAME" \
    --region $REGION \
    --query 'id' \
    --output text)
else
  echo "🔁 Reusing existing REST API Gateway: $API_ID"
fi

# === STEP 3: Get Root Resource ID ===
ROOT_ID=$("$AWS" apigateway get-resources \
  --rest-api-id $API_ID \
  --region $REGION \
  --query "items[?path=='/'].id" \
  --output text)

# === STEP 4: Create Resource ===
RESOURCE_ID=$("$AWS" apigateway create-resource \
  --rest-api-id $API_ID \
  --parent-id $ROOT_ID \
  --path-part "cart" \
  --region $REGION \
  --query 'id' \
  --output text)

# === STEP 5: Create or Update Lambda Function ===
echo "🚀 Creating or Updating Lambda: $FUNC_NAME"

"$AWS" lambda get-function --function-name $FUNC_NAME --region $REGION > /dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "🔁 Function exists. Updating code..."
  "$AWS" lambda update-function-code \
    --function-name $FUNC_NAME \
    --s3-bucket $S3_BUCKET \
    --s3-key products-service.jar \
    --region $REGION
else
  echo "🆕 Creating new Lambda function..."
  "$AWS" lambda create-function \
    --function-name $FUNC_NAME \
    --runtime java21 \
    --vpc-config SubnetIds=[$SUBNET_IDS],SecurityGroupIds=[$SECURITY_GROUP_ID] \
    --handler $HANDLER \
    --memory-size 1024 \
    --timeout 30 \
    --code S3Bucket=$S3_BUCKET,S3Key=products-service.jar \
    --role $ROLE_ARN \
    --environment Variables="{SPRING_CLOUD_FUNCTION_DEFINITION=$FUNCTION_DEF,MAIN_CLASS=$MAIN_CLASS}" \
    --region $REGION
fi

# === STEP 6: Create Methods and Integrations ===
for entry in "${ROUTES[@]}"; do
  IFS='|' read -r METHOD PATH <<< "$entry"
  echo "🔧 Creating method: $METHOD /$PATH"

  "$AWS" apigateway put-method \
    --rest-api-id $API_ID \
    --resource-id $RESOURCE_ID \
    --http-method $METHOD \
    --authorization-type "NONE" \
    --region $REGION

  "$AWS" apigateway put-integration \
    --rest-api-id $API_ID \
    --resource-id $RESOURCE_ID \
    --http-method $METHOD \
    --type AWS_PROXY \
    --integration-http-method POST \
    --uri "arn:aws:apigateway:$REGION:lambda:path/2015-03-31/functions/arn:aws:lambda:$REGION:204620195036:function:$FUNC_NAME/invocations" \
    --region $REGION

  echo "🔓 Adding permission for API Gateway to invoke Lambda"
  "$AWS" lambda add-permission \
    --function-name "$FUNC_NAME" \
    --statement-id "apigateway-${METHOD}-${PATH}-invoke" \
    --action lambda:InvokeFunction \
    --principal apigateway.amazonaws.com \
    --source-arn "arn:aws:execute-api:$REGION:204620195036:$API_ID/*/$METHOD/cart" \
    --region $REGION

  sleep 3
done

# === STEP 7: Deploy API ===
echo "🚀 Deploying REST API to stage: $STAGE_NAME"
"$AWS" apigateway create-deployment \
  --rest-api-id $API_ID \
  --stage-name $STAGE_NAME \
  --region $REGION

echo "🎉 REST API deployed and integrated with Lambda!"
echo "🌐 Base URL: https://$API_ID.execute-api.$REGION.amazonaws.com/$STAGE_NAME/cart"
