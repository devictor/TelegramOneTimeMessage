import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OTMBot extends TelegramLongPollingBot {

    private HashMap<String, String> messages = new HashMap<>();

    @Override
    public String getBotToken() {
        return "441261113:AAHU-q3rtES-274YbuslgMWwuuM3GpvGU4M";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                if (update.hasMessage()) {
                    Message message = update.getMessage();
                    if (message.hasText()) {
                        handleIncomingMessage(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleIncomingMessage(Message message) {
        String[] content = message.getText().split(" ");
        SendMessage output = new SendMessage();
        output.setChatId(message.getChatId());
        switch (content[0]) {
            case "/start": {
                output.setText("Welcome to One Time Message Service!\nIf you want to send OTM, type '/send'. If you want to read" +
                        " OTN - type '/read'");
                break;
            }
            case "/send": {
                if (content.length > 1) {
                    String toSave = "";
                    for (int i = 1; i < content.length; i++) {
                        toSave += content[i] + " ";
                    }
                    toSave = toSave.trim();
                    String key = saveOTM(toSave);
                    output.setText("Message saved! Key for your message is " + key);
                } else {
                    output.setText("To save OneTimeMessage use '/send <TEXT>'");
                }
                break;
            }
            case "/read": {
                if (content.length > 1) {
                    String key = content[1];
                    output.setText(getOTM(key));
                } else {
                    output.setText("To read OneTimeMessage use '/read <KEY>'");
                }
                break;
            }
            case "/help": {
                output.setText("'/start' - welcome message.\n" +
                        "'/send <TEXT>' - send OneTimeMessage.\n" +
                        "'/read <KEY>' - read OneTimeMessagw with given key. The message will be deleted after reading.");
            }
            default: {
                output.setText("Unknown command! Try '/help'");
            }

        }
        try {
            output.enableMarkdown(true);

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            output.setReplyMarkup(replyKeyboardMarkup);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);

            // Создаем список строк клавиатуры
            List<KeyboardRow> keyboard = new ArrayList<>();

            // Первая строчка клавиатуры
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            // Добавляем кнопки в первую строчку клавиатуры
            keyboardFirstRow.add("/send");
            keyboardFirstRow.add("/read");
            keyboardFirstRow.add("/help");

            // Добавляем все строчки клавиатуры в список
            keyboard.add(keyboardFirstRow);
            // и устанваливаем этот список нашей клавиатуре
            replyKeyboardMarkup.setKeyboard(keyboard);

            sendMessage(output);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String saveOTM(String toSave) {
        String key = (int)(Math.random()*1000) + Long.toHexString(Double.doubleToLongBits(Math.random()));
        while (messages.containsKey(key)) {
            key = (int)(Math.random()*1000) + Long.toHexString(Double.doubleToLongBits(Math.random()));
        }
        messages.put(key, toSave);
        return key;
    }

    public String getOTM(String key) {
        if (!messages.containsKey(key)) {
            return "There is no such OneTimeMessage! Try another key!";
        }
        String mess = "Your OTM:\n" + messages.get(key);
        messages.remove(key);
        return mess;
    }

    @Override
    public String getBotUsername() {
        return "OneTimeMessageBot";
    }
}
