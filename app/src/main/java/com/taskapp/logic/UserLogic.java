package com.taskapp.logic;

import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.User;

public class UserLogic {
    private final UserDataAccess userDataAccess;

    public UserLogic() {
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param userDataAccess
     */
    public UserLogic(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    /**
     * ユーザーのログイン処理を行います。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByEmailAndPassword(String, String)
     * @param email ユーザーのメールアドレス
     * @param password ユーザーのパスワード
     * @return ログインしたユーザーの情報
     * @throws AppException メールアドレスとパスワードが一致するユーザーが存在しない場合にスローされます
     */
public User login(String email, String password) throws AppException {
        // 入力チェック
        if (email == null || email.isEmpty()) {
            throw new AppException("メールアドレスを入力してください");
        }
        if (password == null || password.isEmpty()) {
            throw new AppException("パスワードを入力してください");
        }

        // データベースからユーザーを取得
        User user = userDataAccess.findByEmailAndPassword(email.trim(), password.trim());

        // ユーザーが見つからない場合
        if (user == null) {
            throw new AppException("既に登録されているメールアドレス、パスワードを入力してください");
        }

        // 正常にログインできた場合
        return user;
    }
}