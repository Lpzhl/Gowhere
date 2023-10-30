package com.atguigu.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

@Component
public class EmailVca implements Callable<Void> {

    private String toEmail;
    private String generatedCode;
    private HttpSession session;
    private String token;
    public static LocalDateTime expirationTime;
    public static String formattedExpirationTime;

    @Autowired
    public EmailVca(HttpSession session) {
        this.session = session;
    }

    public EmailVca(String toEmail, String generatedCode, String token) {
        this.toEmail = toEmail;
        this.generatedCode = generatedCode;
        this.token = token;
    }


    // 验证码有效期限，以分钟为单位
    public static final int CODE_VALIDITY_MINUTES = 1;

    public EmailVca(String toEmail, String generatedCode, HttpSession session) {
        this.toEmail = toEmail;
        this.generatedCode = generatedCode;
        this.session = session;
    }


    public EmailVca(String toEmail, String generatedCode) {
        this.toEmail = toEmail; // 接收者
        this.generatedCode = generatedCode;// 验证码
    }
    public Void call() throws Exception {
        // 从 email.properties 文件中读取邮件服务器配置信息
        //我们首先创建了一个 Properties 对象 properties，然后使用 FileInputStream 来打开配置文件，将其读取到输入流 input 中。
        // 接下来，我们使用 properties 的 load 方法将输入流中的数据加载到 properties 对象中。
        //我们使用了 try-with-resources 语句，它会自动关闭输入流 input
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        // 获取发送方的邮箱地址和密码
        String fromEmail = properties.getProperty("mail.from.email");
        String emailPassword = properties.getProperty("mail.from.password");

        // 创建一个用于发送邮件的 session  JavaMail API
        Session session1 = Session.getInstance(properties, new Authenticator() {
            // 创建一个新的 Authenticator 对象来进行身份验证
            protected PasswordAuthentication getPasswordAuthentication() {
                // 返回包含发送方邮箱地址和密码的新的 PasswordAuthentication 对象
                return new PasswordAuthentication(fromEmail, emailPassword);
            }
        });

        // 组装邮件内容并发送邮件
        try {
            // 创建一个新的 MimeMessage 对象
            Message message = new MimeMessage(session1);
            // 设置邮件的发送方
            message.setFrom(new InternetAddress(fromEmail));
            // 设置邮件的接收方
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            // 设置邮件的标题
            message.setSubject("【HOPE科技】");
            // 设置邮件的正文
            message.setText("验证码: " + " "+generatedCode +" "+ "用于QQ邮箱身份验证，3分钟内有效，请勿泄露和转发。如非本人操作，请忽略此短信。");
            System.out.println(generatedCode);
            // 发送邮件
            Transport.send(message);
            System.out.println("发送成功!");

            expirationTime  = LocalDateTime.now().plusMinutes(CODE_VALIDITY_MINUTES);

            // 生成令牌并传递给前端
            return null;

        } catch (MessagingException e) {
            // 如果发送过程中出现异常，则打印异常堆栈信息
            e.printStackTrace();
        }


        // 返回空值
        return null;
    }

    public static String generateToken(String generatedCode) {
        return generatedCode + "," + expirationTime.toString();
    }

    public static String getCodeFromToken(String token) {
        // 解析令牌并提取验证码部分
        String[] tokenParts = token.split(",",2);
        System.out.println("tokenParts = " + token);
        if (tokenParts.length == 2) {
            return tokenParts[0];
        } else {
            return null;
        }
    }

    public static LocalDateTime getExpirationTimeFromToken(String token) {
        // 解析令牌并提取过期时间部分
        String[] tokenParts = token.split(",",2);
        System.out.println("tokenParts = " + token);
        if (tokenParts.length == 2) {
            String expirationTimeString = tokenParts[1];
            System.out.println("expirationTimeString = " + expirationTimeString);
            try {
                return LocalDateTime.parse(expirationTimeString);
            } catch (DateTimeParseException e) {
                // 如果解析失败，可以在这里处理异常
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }


    // 获取令牌
    public String getToken() {
        return token;
    }

        // 生成一个随机六位数验证码
    //定义了一个公共静态方法 generateRandomCode()，该方法的返回值类型为 String 类型。
    public static String generateRandomCode() {
        Random random = new SecureRandom();//创建了一个名为 random 的 Random 对象，这个对象用于生成伪随机数字序列。
        /*
        通过调用 Random 对象的 nextInt(int bound) 方法，生成一个范围在 0 到 (bound-1) 之间的一个整数。
        在这里，bound 参数被设置成了 1_000_000，所以随机生成的数字 code 取值范围为：[0, 999999] ，即六位数。
         */
        int code = random.nextInt(1_000_000);
        /*
        然后将生成的随机数 code 格式化为六位字符串，使用了 String 类型的 format() 方法，
        其中 "%06d" 表示转换成六位的数字字符串，不足六位则左侧补 0。
         */
        return String.format("%06d", code);
    }
}