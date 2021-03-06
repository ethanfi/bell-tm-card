package cn.edu.bnuz.bell.tm.card.api

import cn.edu.bnuz.bell.card.ReissueReviewerService
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.StateObject
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.config.DefaultStateMachinePersistConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.persist.StateMachinePersister

@Configuration
@Import([CardReissueStateMachineConfiguration, DefaultStateMachinePersistConfiguration])
class WorkflowConfiguration {
    @Bean
    DomainStateMachineHandler domainStateMachineHandler(
            StateMachine<State, Event> stateMachine,
            StateMachinePersister<State, Event, StateObject> persister,
            ReissueReviewerService reviewerService) {
        new DomainStateMachineHandler(stateMachine, persister, reviewerService)
    }
}