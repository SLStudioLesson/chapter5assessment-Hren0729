package com.taskapp.logic;

import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.User;

public class UserLogic {
    private final UserDataAccess userDataAccess;

    public UserLogic() {
        userDataAccess = new UserDataAccess();
    }

    // 依存性注入用のコンストラクタ
    public UserLogic(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    /**
     * ユーザーのログイン処理を行います。
     *
     * @param email ユーザーのメールアドレス
     * @param password ユーザーのパスワード
     * @return ログインしたユーザーの情報
     * @throws AppException メールアドレスとパスワードが一致するユーザーが存在しない場合にスローされます
     */
    public User login(String email, String password) throws AppException {
        // メールアドレスとパスワードでユーザーを検索
        User user = userDataAccess.findByEmailAndPassword(email, password);

        // ユーザーが見つからない場合
        if (user == null) {
            throw new AppException("メールアドレスまたはパスワードが間違っています。");
        }

        System.out.println("ユーザー名：" + user.getName() + "でログインしました。");
        return user;
    }
}
