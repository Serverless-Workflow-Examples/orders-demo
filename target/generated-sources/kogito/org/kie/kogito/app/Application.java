package org.kie.kogito.app;

import org.kie.kogito.Config;
import org.kie.kogito.uow.UnitOfWorkManager;

@javax.inject.Singleton()
public class Application implements org.kie.kogito.Application {

    @javax.inject.Inject()
    javax.enterprise.inject.Instance<org.kie.kogito.event.EventPublisher> eventPublishers;

    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "kogito.service.url")
    java.util.Optional<java.lang.String> kogitoService;

    @javax.inject.Inject()
    org.kie.kogito.Config config;

    Processes processes = new Processes(this);

    RuleUnits ruleUnits = new RuleUnits(this);

    DecisionModels decisionModels = new DecisionModels();

    public Config config() {
        return config;
    }

    public UnitOfWorkManager unitOfWorkManager() {
        return config().process().unitOfWorkManager();
    }

    @javax.annotation.PostConstruct()
    public void setup() {
        if (config().process() != null) {
            if (eventPublishers != null) {
                eventPublishers.forEach(publisher -> unitOfWorkManager().eventManager().addPublisher(publisher));
            }
            unitOfWorkManager().eventManager().setService(kogitoService.orElse(""));
            unitOfWorkManager().eventManager().setAddons(config().addons());
        }
        if (config().decision() != null) {
            decisionModels.init(this);
        }
    }

    public Processes processes() {
        return processes;
    }

    public RuleUnits ruleUnits() {
        return ruleUnits;
    }

    public DecisionModels decisionModels() {
        return decisionModels;
    }
}
