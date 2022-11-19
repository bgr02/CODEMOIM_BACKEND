package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Email;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.exception.BadRequestException;
import com.sideproject.codemoim.exception.InvalidSecretKeyException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.repository.EmailRepository;
import com.sideproject.codemoim.repository.ProfileRepository;
import com.sideproject.codemoim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final CustomProperties customProperties;

    public boolean duplicateCheck(String email) throws UnsupportedEncodingException {
        String searchEmail = email;

        if (specialWordExist(email)) {
            searchEmail = URLDecoder.decode(searchEmail, "UTF-8");
        }

        Optional<Email> optionalEmail = Optional.ofNullable(emailRepository.searchEmailByEmail(searchEmail));

        return optionalEmail.isPresent();
    }

    @Transactional
    public String sendVerifyEmail(Map<String, Object> emailInfo, Long userId) throws MessagingException {
        long profileId = (int) emailInfo.get("profileId");
        final String[] result = new String[1];

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(userId);

                User user = optionalUser.orElseThrow(() -> {
                    throw new UsernameNotFoundException("User Not Exist");
                });

                if(user.getStatus() == 0) {
                    String email = (String) emailInfo.get("email");
                    String subject = "CODEMOIM 이메일 인증 안내입니다.";
                    String secretKey = registerEmailSecretKey(userId);
                    String text = createVerifyMailText(user.getUsername(), secretKey);

                    try {
                        createMail(email, subject, text);
                    } catch (MessagingException exception) {
                        StackTraceElement[] ste = exception.getStackTrace();

                        String className = ste[0].getClassName();
                        String methodName = ste[0].getMethodName();
                        int lineNumber = ste[0].getLineNumber();
                        String fileName = ste[0].getFileName();

                        log.error("MessagingException: " + exception.getMessage());
                        log.error("[Class] => " + className + ", [Method] => " + methodName + ", [File] => " + fileName + ", [Line] => " + lineNumber);
                    }

                    result[0] = "success";
                } else if(user.getStatus() == 1){
                    result[0] = "authenticated";
                } else {
                    result[0] = "withdrawal";
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return result[0];
    }

    private void createMail(String email, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(text,true);

        javaMailSender.send(mimeMessage);
    }

    private String registerEmailSecretKey(Long userId) {
        Email email = emailRepository.searchEmailByUserId(userId);

        String secretKey = "";

        boolean keyFlag = true;

        while(keyFlag) {
            secretKey = UUID.randomUUID().toString();

            Email duplicateKeyEmail = emailRepository.searchEmailBySecretKey(secretKey);
            if(duplicateKeyEmail == null) {
                keyFlag = false;
            }
        }

        email.updateSecretKey(secretKey);

        LocalDateTime expiredDate = LocalDateTime.now().plusMinutes(10);
        email.updateExpiredDate(expiredDate);

        return secretKey;
    }

    private String createVerifyMailText(String username, String secretKey) {
        String protocol = customProperties.getCookieConfig().getProtocol();
        String domain = customProperties.getCookieConfig().getBackSubDomain();

        StringBuilder mainText = new StringBuilder();

        mainText.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div style=\"width: 400px; height: 600px; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">\n" +
                "        <div style=\"background-color: #34495E; color: white; font-size: 35px; font-weight: bold; width: 100%; text-align: center; margin-bottom: 30px;\">\n" +
                "            CODEMOIM\n" +
                "        </div>\n" +
                "        <div style=\"text-align: center;\">\n" +
                "            <h1 style=\"font-size: 30px; font-weight: 400;\">\n" +
                "                <span style=\"color: #34495E\">\n" +
                "                    이메일 주소 인증\n" +
                "                </span>\n" +
                "            </h1>            \n" +
                "            <p style=\"font-size: 15px; line-height: 30px; margin-top: 50px; margin-bottom: 50px; text-align: left; width: 320px; margin-left: 40px;\">\n" +
                "                안녕하세요. <span style=\"font-weight: bold;\">" + username + "</span>님 CODEMOIM입니다.<br />\n" +
                "                아래의 버튼을 클릭하여 인증을 완료 해주세요.<br />    \n" +
                "                해당 인증은 10분동안 유효합니다.\n" +
                "                감사합니다.\n" +
                "            </p>            \n" +
                "            <a href=" + protocol + "://" + domain + "/api/email/verify-key?key=" + secretKey + "\" target=\"_blank\">\n" +
                "                <p style=\"display: inline-block; color: white; width: 150px; height: 50px; background: #34495E; font-size: 18px; line-height: 50px; margin-bottom: 30px;\">\n" +
                "                    이메일 인증\n" +
                "                </p>\n" +
                "            </a>\n" +
                "        </div>        \t\n" +
                "        <div style=\"border-top: 1px solid #DDD; padding: 5px;\"></div>\n" +
                "\t</div>\n" +
                "</body>\n" +
                "</html>");

        return mainText.toString();
    }

    @Transactional
    public String verifyKey(String secretKey) {
        Optional<Email> email = Optional.ofNullable(emailRepository.verifySecretKey(secretKey));

        final String[] key = new String[1];

        email.ifPresentOrElse(email1 -> {
            User user = email1.getUser();

            user.updateStatus((byte) 1);

            String newSecretKey = UUID.randomUUID().toString();

            email1.updateSecretKey(newSecretKey);
            email1.updateExpiredDate(LocalDateTime.now().plusMinutes(1));

            key[0] = newSecretKey;
        }, () -> {
            key[0] = null;
        });

        return key[0];
    }

    @Transactional
    public void keyExpire(Map<String, Object> keyInfo) {
        String key = (String) keyInfo.get("key");

        Optional<Email> optionalEmail = Optional.ofNullable(emailRepository.verifySecretKey(key));

        optionalEmail.ifPresentOrElse(email -> {
            email.updateExpiredDate(LocalDateTime.now());
        }, () -> {
            throw new InvalidSecretKeyException("Invalid Secret Key");
        });
    }

    @Transactional
    public boolean sendFindPasswordEmail(Map<String, Object> emailInfo) {
        String email = (String) emailInfo.get("email");

        Optional<Email> optionalEmail = Optional.ofNullable(emailRepository.searchEmailByEmail(email));

        final boolean[] sendFlag = {false};

        optionalEmail.ifPresentOrElse(email1 -> {
            String secretKey = registerUserSecretKey(email1.getUser());
            String text = createFindPasswordMailText(email1.getUser().getUsername(), secretKey);

            try {
                createMail(email, "CODEMOIM 비밀번호 찾기 안내입니다.", text);
                sendFlag[0] = true;
            } catch (MessagingException exception) {
                StackTraceElement[] ste = exception.getStackTrace();

                String className = ste[0].getClassName();
                String methodName = ste[0].getMethodName();
                int lineNumber = ste[0].getLineNumber();
                String fileName = ste[0].getFileName();

                log.error("MessagingException: " + exception.getMessage());
                log.error("[Class] => " + className + ", [Method] => " + methodName + ", [File] => " + fileName + ", [Line] => " + lineNumber);
            }
        }, () -> {
            try {
                throw new MessagingException("Email Not Invalid");
            } catch (MessagingException exception) {
                StackTraceElement[] ste = exception.getStackTrace();

                String className = ste[0].getClassName();
                String methodName = ste[0].getMethodName();
                int lineNumber = ste[0].getLineNumber();
                String fileName = ste[0].getFileName();

                log.error("MessagingException: " + exception.getMessage());
                log.error("[Class] => " + className + ", [Method] => " + methodName + ", [File] => " + fileName + ", [Line] => " + lineNumber);
            }
        });

        return sendFlag[0];
    }

    private String registerUserSecretKey(User user) {
        String secretKey = "";

        boolean keyFlag = true;

        while(keyFlag) {
            secretKey = UUID.randomUUID().toString();

            Email duplicateKeyEmail = emailRepository.searchEmailBySecretKey(secretKey);
            if(duplicateKeyEmail == null) {
                keyFlag = false;
            }
        }

        user.updatePasswordChangeKey(secretKey);
        user.updatePasswordChangeKeyExpiredDate(LocalDateTime.now().plusMinutes(10));

        return secretKey;
    }

    private String createFindPasswordMailText(String username, String secretKey) {
        String protocol = customProperties.getCookieConfig().getProtocol();
        String domain = customProperties.getCookieConfig().getFrontSubDomain();

        StringBuilder mainText = new StringBuilder();

        mainText.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div style=\"width: 400px; height: 600px; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">\n" +
                "        <div style=\"background-color: #34495E; color: white; font-size: 35px; font-weight: bold; width: 100%; text-align: center; margin-bottom: 30px;\">\n" +
                "            CODEMOIM\n" +
                "        </div>\n" +
                "        <div style=\"text-align: center;\">\n" +
                "            <h1 style=\"font-size: 30px; font-weight: 400;\">\n" +
                "                <span style=\"color: #34495E\">\n" +
                "                    비밀번호 변경\n" +
                "                </span>\n" +
                "            </h1>            \n" +
                "            <p style=\"font-size: 15px; line-height: 30px; margin-top: 50px; margin-bottom: 50px; text-align: left; width: 380px; margin-left: 10px;\">\n" +
                "                안녕하세요. <span style=\"font-weight: bold;\">" + username + "</span>님 CODEMOIM입니다.<br />\n" +
                "                아래의 버튼을 클릭하여 비밀번호 변경을 진행해주세요.<br />    \n" +
                "                해당 비밀번호 변경은 10분동안 유효합니다.\n" +
                "                감사합니다.\n" +
                "            </p>            \n" +
                "            <a href=" + protocol + "://" + domain + "/passwordChange?key=" + secretKey + "\" target=\"_blank\">\n" +
                "                <p style=\"display: inline-block; color: white; width: 150px; height: 50px; background: #34495E; font-size: 18px; line-height: 50px; margin-bottom: 30px;\">\n" +
                "                    비밀번호 변경\n" +
                "                </p>\n" +
                "            </a>\n" +
                "        </div>        \t\n" +
                "        <div style=\"border-top: 1px solid #DDD; padding: 5px;\"></div>\n" +
                "\t</div>\n" +
                "</body>\n" +
                "</html>");

        return mainText.toString();
    }

    private boolean specialWordExist(String keyword) {
        boolean exist = false;

        String[] checkWords = {"%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29", "%2A", "%2B",
                "%2C", "%2D", "%2E", "%2F", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F", "%40", "%5B", "%5C", "%5D",
                "%5E", "%5F", "%60", "%7B", "%7C", "%7D", "%7E"};

        for (String checkWord : checkWords) {
            if (keyword.contains(checkWord)) {
                exist = true;
                break;
            }
        }

        return exist;
    }

}
