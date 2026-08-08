package t3digitalgroup.vehnixauto.server.utils.scheduler

import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@Component
class PaymentTimeoutJob(
    private val paymentTaskService: PaymentTaskService,
) : Job {
    override fun execute(context: JobExecutionContext) {
        val reference = context.mergedJobDataMap.getString("reference")
        runBlocking {
            paymentTaskService.cancelPendingPayment(reference)
        }
    }
}
