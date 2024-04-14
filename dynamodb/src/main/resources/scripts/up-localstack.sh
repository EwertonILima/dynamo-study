# Create Table
aws --endpoint="http://localhost:4566" dynamodb create-table \
  --region "sa-east-1" \
  --table-name "player_history" \
  --attribute-definitions \
    "AttributeName=player_id,AttributeType=S" \
    "AttributeName=game_id,AttributeType=S" \
  --key-schema \
    "AttributeName=player_id,KeyType=HASH" \
    "AttributeName=game_id,KeyType=RANGE" \
  --provisioned-throughput \
      "ReadCapacityUnits=5,WriteCapacityUnits=5"

# Create Table
aws --endpoint="http://localhost:4566" dynamodb create-table \
  --region "sa-east-1" \
  --table-name "tbjw6003_txa_prod" \
  --attribute-definitions \
    "AttributeName=cod_prod_crto_cred,AttributeType=N" \
    "AttributeName=dat_atui,AttributeType=S" \
  --key-schema \
    "AttributeName=cod_prod_crto_cred,KeyType=HASH" \
    "AttributeName=dat_atui,KeyType=RANGE" \
  --provisioned-throughput \
      "ReadCapacityUnits=5,WriteCapacityUnits=5"