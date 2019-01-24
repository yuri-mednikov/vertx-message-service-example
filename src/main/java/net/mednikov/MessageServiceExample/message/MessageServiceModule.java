package net.mednikov.MessageServiceExample.message;

import com.google.inject.AbstractModule;

public class MessageServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();
        bind(IMessageDao.class).toInstance(new MessageDaoImpl());
    }
}
