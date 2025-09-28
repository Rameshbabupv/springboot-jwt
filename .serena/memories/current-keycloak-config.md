# Current Keycloak Configuration

## CURRENT STATE:
- **Keycloak**: Running on port 8090
- **Realm**: `systech`
- **Client**: `systech-hrms-client`
- **User**: `babu.systech`

## Previous Configuration (for reference):
- **Realm**: `nexus-dev` 
- **Client**: `nexus-web-app`
- **User**: `nexus-user`

## Configuration Files That Need Updates:
1. `src/main/resources/application.yml` - JWT issuer URI
2. `src/main/resources/application-dev.yml` - Development overrides
3. `scripts/quick-test.sh` - Test scripts
4. `scripts/test-jwt.sh` - JWT testing
5. Any documentation referencing the old realm/client

## New URLs:
- **Issuer URI**: `http://localhost:8090/realms/systech`
- **Token Endpoint**: `http://localhost:8090/realms/systech/protocol/openid-connect/token`
- **Admin Console**: `http://localhost:8090/admin/master/console/#/systech`

## Required Updates:
- Update Spring Security OAuth2 configuration
- Update test scripts with new client ID
- Update documentation
- Verify role mappings in new realm