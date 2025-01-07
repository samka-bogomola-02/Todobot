package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            if (update.message() != null) {
                Message message = update.message();
                String text = message.text();
                long chatId = message.chat().id(); // Получаем ID чата

                if ("/start".equals(text)) {
                    String welcomeMessage = "Привет! Я Todobot - твой помошник в ежедневных делах." +
                            "\nСо мной тебе не нужно будет зацикливаться на мелочах, " +
                            "ведь ты создан для великих дел.";

                    SendMessage sendMessage = new SendMessage(chatId, welcomeMessage);
                    telegramBot.execute(sendMessage);
                }

                if ("/notes".equals(text)) {
                    String messageNotification = "Добавь свою задачу в формате: 'ДД.ММ.ГГГГ ЧЧ:ММ Задача";
                    sendMessage(chatId, messageNotification);
                }
                // Обработка сообщения с задачей
                String regex = "(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)";
                Pattern pattern = Pattern.compile(regex);
                if (text.matches(regex)) {

                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        String dateTimeString = matcher.group(1); // Дата и время
                        String notificationText = matcher.group(3); // Текст уведомления

                        // Преобразование строки в LocalDateTime
                        LocalDateTime scheduledTime = null;
                        try {
                            scheduledTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                        } catch (DateTimeParseException e) {
                            sendMessage(chatId, "Неверный формат даты и времени. Пример: 20.03.2022 15:30 Задача");
                            return;
                        }

                        // Создание объекта NotificationTask
                        NotificationTask notificationTask = new NotificationTask();
                        notificationTask.setChatId(chatId);
                        notificationTask.setNotificationText(notificationText);
                        notificationTask.setScheduledTime(scheduledTime);
                        notificationTask.setCreatedAt(LocalDateTime.now());

                        // Сохранение задачи в базе данных
                        notificationTaskRepository.save(notificationTask);

                        // Форматирование даты и времени для отправки пользователю
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
                        String formattedScheduledTime = scheduledTime.format(formatter);


                        String confirmationMessage = "Задача добавлена: " + notificationText + " в " + formattedScheduledTime;
                        sendMessage(chatId, confirmationMessage);
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        try {
            telegramBot.execute(sendMessage);
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при отправке сообщения, повторите попытку");
            logger.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }
}
