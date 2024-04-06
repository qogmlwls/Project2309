package com.example.project2309.data;

public class HTTPData {

    // 이건 지우도록
    // Http 문제 발생하면 출력할 메세지니까, Message에 들어가는게 더 좋을 듯.
    public static String 통신네트워크에러발생 = "onNetworkError";
    public static String 응답실패에러발생 = "onResponseFail";


    public static class 회원가입이메일인증정보{

        public String catagory = "Join";
        public String 중복검사path = "EmaiIsDuplicate";

        public String 중복검사bodykeyValue = "email";
        public String 이메일미입력상태메세지 = "이메일(아이디)를 입력해주세요.";

        public String 중복이메일상태메세지 = "이미 가입에 사용된 이메일(아이디)입니다.";

        public String 중복검사통과이메일상태메세지 = "사용 가능한 이메일(아이디)입니다.";



        public String 인증요청bodykeyValue = "email";

        public String 인증요청path = "RequestAuthCodeMail";


        public String 인증코드미입력상태메세지 = "인증코드를 입력해주세요.";



        public String 인증확인요청EmailBodyKeyValue = "email";
        public String 인증확인요청CodeBodyKeyValue = "code";

        public String 인증확인요청path = "AuthCheck";

        public String 인증코드일치성검사미통과메세지 = "인증코드를 다시 입력해주세요.";
        public String 인증코드일치성검사통과메세지 = "인증이 완료되었습니다.";

        public String 인증도중이메일변경시출력되는메세지 = "중복검사에 통과한 이메일과 현재 입력된 이메일 값이 다릅니다. 다시 인증해주세요.";

    }

    public static class 로그인정보{
        public static String catagory = "Login";
        public static String path = "RequestLogin";


    }

}

