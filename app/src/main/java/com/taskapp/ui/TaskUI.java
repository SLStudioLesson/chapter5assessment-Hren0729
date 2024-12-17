package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taskapp.exception.AppException;
import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.User;

public class TaskUI {
    private final BufferedReader reader;
    private final UserLogic userLogic;
    private final TaskLogic taskLogic;
    private User loginUser;

    // デフォルトコンストラクタ
    public TaskUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        userLogic = new UserLogic();
        taskLogic = new TaskLogic();
    }

    // 依存性注入用のコンストラクタ
    public TaskUI(BufferedReader reader, UserLogic userLogic, TaskLogic taskLogic) {
        this.reader = reader;
        this.userLogic = userLogic;
        this.taskLogic = taskLogic;
    }

    /**
     * メインメニューを表示し、ユーザー入力に基づいてアクションを実行します。
     */
    public void displayMenu() {
        System.out.println("タスク管理アプリケーションにようこそ!!");
        inputLogin();
        boolean isRunning = true;

        while (isRunning) {
            try {
                System.out.println("以下1~3のメニューから好きな選択肢を選んでください。");
                System.out.println("1. タスク一覧, 2. タスク新規登録, 3. ログアウト");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();
                System.out.println();

                switch (selectMenu) {
                    case "1":
                    taskLogic.showAll(loginUser);
                        break;
                    case "2":
                        inputNewInformation(); // 新規タスク登録を呼び出し
                        break;
                    case "3":
                        System.out.println("ログアウトしました。");
                        isRunning = false; // ループ終了
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                System.out.println("入力エラーが発生しました。再度お試しください。");
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからログイン情報を受け取り、ログイン処理を実行します。
     */
    public void inputLogin() {
        boolean isLoginSuccessful = false;

        while (!isLoginSuccessful) {
            try {
                System.out.print("メールアドレスを入力してください：");
                String email = reader.readLine();
                System.out.print("パスワードを入力してください：");
                String password = reader.readLine();

                // ログイン情報を呼び出す
                loginUser = userLogic.login(email, password);
                System.out.println("ユーザー名：" + loginUser.getName() + "でログインしました。");
                isLoginSuccessful = true; // ログイン成功
                
            } catch (IOException e) {
                System.out.println("入力エラーが発生しました。再度お試しください。");
            } catch (AppException e) {
                System.out.println("ログインエラー：" + e.getMessage());
            }
        }
    }

    /**
     * ユーザーから新規タスク情報を受け取り、タスクを登録します。
     */
    public void inputNewInformation() {
 
    }

    /**
     * 文字列が正の数値であるかを判定します。
     *
     * @param inputText チェック対象の文字列
     * @return 正の数値ならtrue、それ以外はfalse
     */
    public boolean isNumeric(String inputText) {
        if (inputText == null || inputText.isEmpty()) {
            return false;
        }
        try {
            int num = Integer.parseInt(inputText);
            return num > 0; // 負の数や0は無効
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * メインメソッド - アプリケーションを起動します。
     */
    public static void main(String[] args) {
        TaskUI taskUI = new TaskUI();
        taskUI.inputLogin();
        taskUI.displayMenu();
    }
}
