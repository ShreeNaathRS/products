#!/bin/bash

# === CONFIGURATION ===
JAR_PATH="target/products-0.0.1-SNAPSHOT.jar"
S3_BUCKET="products-lambda-jar"
ROLE_ARN="arn:aws:iam::204620195036:role/LambdaExec"
REGION="ap-south-1"
API_NAME="swift-cart"
STAGE_NAME="swift-cart"
ISSUER_URL="https://dev-z98mxvin.us.auth0.com"
AUDIENCE="https://swiftcart/api"
#IDENTITY_SOURCE="\$request.header.Authorization"
VPC_ID="vpc-0c0a5a82dca767baf"
SUBNET_IDS="subnet-053620b9c1566d4d8,subnet-0ed513ccf15f94bbe,subnet-0e78173bed16975a7"
SECURITY_GROUP_ID="sg-0a36b067f010159dc"
AWS="/c/Program Files/Amazon/AWSCLIV2/aws"

# === FUNCTION CONFIGURATION ===
functions=(
  #"updateCart|PUT|cart"
  #"createCart|POST|cart"
  "generateToken|POST|token"
)

# === STEP 1: Upload JAR to S3 ===
echo "📦 Uploading JAR to S3..."
"$AWS" s3 cp $JAR_PATH s3://$S3_BUCKET/products-service.jar --region $REGION

# === STEP 2: Create API Gateway (only once) ===
echo "🔍 Checking if API already exists..."
API_ID=$("$AWS" apigatewayv2 get-apis --region $REGION --query "Items[?Name=='$API_NAME'].ApiId" --output text)

if [ -z "$API_ID" ]; then
  echo "🆕 Creating new API Gateway: $API_NAME"
  API_ID=$("$AWS" apigatewayv2 create-api \
    --name "$API_NAME" \
    --protocol-type HTTP \
    --region $REGION \
    --query 'ApiId' \
    --output text)
else
  echo "🔁 Reusing existing API Gateway: $API_ID"
fi

# === STEP 3: Create Stage (only once) ===
echo "🔍 Checking if stage '$STAGE_NAME' exists..."
STAGE_EXISTS=$("$AWS" apigatewayv2 get-stages --api-id $API_ID --region $REGION --query "Items[?StageName=='$STAGE_NAME'].StageName" --output text)

if [ -z "$STAGE_EXISTS" ]; then
  echo "🆕 Creating stage '$STAGE_NAME'..."
  "$AWS" apigatewayv2 create-stage \
    --api-id $API_ID \
    --stage-name $STAGE_NAME \
    --auto-deploy \
    --region $REGION
else
  echo "🔁 Reusing existing stage: $STAGE_NAME"
fi

# === STEP 4: Create Shared JWT Authorizer ===
#echo "🔐 Creating shared JWT Authorizer..."
#AUTHORIZER_ID=$("$AWS" apigatewayv2 create-authorizer \
#  --api-id "$API_ID" \
#  --name "SwiftCartJWTAuthorizer" \
#  --authorizer-type JWT \
#  --identity-source "$IDENTITY_SOURCE" \
#  --jwt-configuration Issuer="$ISSUER_URL",Audience="[$AUDIENCE]" \
#  --region "$REGION" \
#  --query 'AuthorizerId' \
#  --output text)

# === STEP 5: Loop through functions ===
for entry in "${functions[@]}"; do
  IFS='|' read -r FUNC METHOD PATH <<< "$entry"
  FUNC_NAME="${FUNC}Function"

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
      --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest \
      --memory-size 1024 \
      --timeout 30 \
      --code S3Bucket=$S3_BUCKET,S3Key=products-service.jar \
      --role $ROLE_ARN \
      --environment Variables="{SPRING_CLOUD_FUNCTION_DEFINITION=$FUNC,MAIN_CLASS=com.swiftcart.products.ProductsApplication}" \
      --region $REGION
  fi

  echo "🔗 Creating integration for $FUNC"
  INTEGRATION_ID=$("$AWS" apigatewayv2 create-integration \
    --api-id $API_ID \
    --integration-type AWS_PROXY \
    --integration-uri "arn:aws:lambda:$REGION:204620195036:function:$FUNC_NAME" \
    --payload-format-version 2.0 \
    --integration-method POST \
    --region $REGION \
    --query 'IntegrationId' \
    --output text)

  echo "🛣️ Creating route: $METHOD /$PATH"
  "$AWS" apigatewayv2 create-route \
    --api-id $API_ID \
    --route-key "$METHOD /$PATH" \
#    --authorization-type JWT \
#    --authorizer-id "$AUTHORIZER_ID" \
    --target "integrations/$INTEGRATION_ID" \
    --region $REGION

  echo "🔓 Adding permission for API Gateway to invoke Lambda"
  "$AWS" lambda add-permission \
    --function-name "$FUNC_NAME" \
    --statement-id "apigateway-${FUNC}-invoke" \
    --action lambda:InvokeFunction \
    --principal apigateway.amazonaws.com \
    --source-arn "arn:aws:execute-api:$REGION:204620195036:$API_ID/*/$METHOD/$PATH" \
    --region $REGION

  sleep 5
done

echo "🎉 All Lambda functions deployed and integrated with API Gateway!"
echo "🌐 Base URL: https://$API_ID.execute-api.$REGION.amazonaws.com/$STAGE_NAME"
