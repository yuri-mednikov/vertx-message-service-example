package net.mednikov.MessageServiceExample.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

public class AuthenticatedUser extends AbstractUser {

    @Override
    protected void doIsPermitted(String s, Handler<AsyncResult<Boolean>> handler) {

    }

    @Override
    public JsonObject principal() {
        return null;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {

    }
}
