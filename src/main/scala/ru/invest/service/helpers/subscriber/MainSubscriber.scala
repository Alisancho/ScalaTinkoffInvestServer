package ru.invest.service.helpers.subscriber

import java.util.concurrent.Executor

class MainSubscriber(override val executor: Executor)
    extends StreamingApiSubscriberAbstract(executor) with StreamingApiSubscriberLogger {}
