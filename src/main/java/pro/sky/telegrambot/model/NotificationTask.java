package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notification_task")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "chat_id", nullable = false)
    private long chatId;
    @Column(name = "notification_text")
    private String notificationText;
    @Column(name = "scheduled_time")
    private int scheduledTime;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public NotificationTask(){
    }
    public NotificationTask( long chatId, String notificationText, int scheduledTime, LocalDateTime createdAt) {
        this.chatId = chatId;
        this.notificationText = notificationText;
        this.scheduledTime = scheduledTime;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getChatId() {
        return chatId;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public int getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(int scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id && chatId == that.chatId
                && scheduledTime == that.scheduledTime
                && Objects.equals(notificationText, that.notificationText)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, notificationText, scheduledTime, createdAt);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", notificationText='" + notificationText + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", createdAt=" + createdAt +
                '}';
    }
}
