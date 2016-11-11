package cn.edu.bnuz.bell.tm.card.api

import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.actions.AutoEntryAction
import cn.edu.bnuz.bell.workflow.config.StandardActionConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.statemachine.action.Action
import org.springframework.statemachine.config.EnableStateMachine
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer

@Configuration
@EnableStateMachine(name='CardReissueFormStateMachine')
@Import(StandardActionConfiguration)
class CardReissueStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<State, Event> {
    @Autowired
    StandardActionConfiguration actions

    @Override
    void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states
            .withStates()
                .initial(State.CREATED)
                .state(State.CREATED,   [actions.logEntryAction()], null)
                .state(State.SUBMITTED, [actions.logEntryAction(), actions.submittedEntryAction()], [actions.workitemProcessedAction()])
                .state(State.CHECKED,   [actions.logEntryAction(), checkedEntryAction()], null)
                .state(State.REJECTED,  [actions.logEntryAction(), actions.rejectedEntryAction()],  [actions.workitemProcessedAction()])
                .state(State.PROGRESS,  [actions.logEntryAction(), progressEntryAction()], null)
                .state(State.FINISHED,  [actions.logEntryAction(), actions.notifySubmitterAction()], null)
    }

    @Override
    void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions
            .withInternal()
                .source(State.CREATED)
                .event(Event.UPDATE)
                .action(actions.logTransitionAction())
                .and()
            .withExternal()
                .source(State.CREATED)
                .event(Event.SUBMIT)
                .target(State.SUBMITTED)
                .and()
            .withExternal()
                .source(State.SUBMITTED)
                .event(Event.ACCEPT)
                .target(State.CHECKED)
                .and()
            .withExternal()
                .source(State.SUBMITTED)
                .event(Event.REJECT)
                .target(State.REJECTED)
                .and()
            .withExternal()
                .source(State.CHECKED)
                .event(Event.ACCEPT)
                .target(State.PROGRESS)
                .and()
            .withExternal()
                .source(State.PROGRESS)
                .event(Event.ACCEPT)
                .target(State.FINISHED)
                .and()
            .withExternal()
                .source(State.PROGRESS)
                .event(Event.REJECT)
                .target(State.CHECKED)
                .and()
            .withInternal()
                .source(State.REJECTED)
                .event(Event.UPDATE)
                .action(actions.logTransitionAction())
                .and()
            .withExternal()
                .source(State.REJECTED)
                .event(Event.SUBMIT)
                .target(State.SUBMITTED)
    }

    @Bean
    Action<State, Event> checkedEntryAction() {
        new AutoEntryAction()
    }

    @Bean
    Action<State, Event> progressEntryAction() {
        new AutoEntryAction()
    }
}