package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class Scheduled {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskRepository notificationTaskRepository;
    @Autowired
    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 * * * * *")
    public void sendScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasks = notificationTaskRepository.findByScheduledTime(now);

        for (NotificationTask task : tasks) {
            telegramBotUpdatesListener.sendMessage(task.getChatId(), task.getNotificationText());
            logger.info("Уведомление отправлено: {}", task.getNotificationText());
            notificationTaskRepository.delete(task);
        }
    }
}
