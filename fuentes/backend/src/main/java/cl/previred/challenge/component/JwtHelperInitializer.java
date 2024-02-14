package cl.previred.challenge.component;

import cl.previred.challenge.config.JwtConfig;
import cl.previred.challenge.helper.JwtHelper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class JwtHelperInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String secret = event.getApplicationContext().getBean(JwtConfig.class).getSecret();
        JwtHelper.setSecretKey(secret);
    }
}
