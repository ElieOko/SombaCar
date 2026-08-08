package t3digitalgroup.vehnixauto.server.utils.scheduler

import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date

@Service
class PaymentScheduler(
    private val scheduler: Scheduler,
) {
    fun scheduleOneShot(taskId: Long, reference: String, minute: Long = 2L) {
        val jobDetail = JobBuilder.newJob(PaymentTimeoutJob::class.java)
            .withIdentity("payment-$taskId-${System.currentTimeMillis()}")
            .usingJobData("reference", reference)
            .storeDurably()
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .withIdentity("payment-trigger-$taskId-${System.currentTimeMillis()}")
            .startAt(Date.from(Instant.now().plusSeconds(minute * 60)))
            .forJob(jobDetail)
            .build()

        scheduler.scheduleJob(jobDetail, trigger)
    }
}
