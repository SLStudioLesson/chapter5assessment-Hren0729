package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taskapp.exception.AppException;
import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.Task;
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

                // メインメニューの選択処理
                switch (selectMenu) {
                    case "1":
                        taskLogic.showAll(loginUser);
                        selectSubMenu();
                        break;
                    case "2":
                        inputNewInformation();
                        break;
                    case "3":
                        System.out.println("ログアウトしました");
                        isRunning = false;
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
     * サブメニューを表示し、タスク情報変更を処理します。
     */
    public void selectSubMenu() {
        boolean isSubMenuRunning = true;

        while (isSubMenuRunning) {
            try {
                System.out.println("以下1~2から好きな選択肢を選んでください。");
                System.out.println("1. タスクのステータス変更, 2. メインメニューに戻る");
                System.out.print("選択肢：");
                String selectSubMenu = reader.readLine();
                System.out.println();

                // サブメニューの選択処理
                switch (selectSubMenu) {
                    case "1":
                        inputChangeInformation();
                        break;
                    case "2":
                        System.out.println("メインメニューに戻ります。");
                        isSubMenuRunning = false;  // メニューを終了してメインに戻る
                        break;
                    default:
                        System.out.println("ステータスは1・2の中から選択してください");
                        break;
                }
            } catch (IOException e) {
                System.out.println("入力エラーが発生しました。再度お試しください。");
                e.printStackTrace();
            }
        }
    }

    /**
     * ユーザーからタスク情報の変更を受け付けます。
     */
    public void inputChangeInformation() {
    try {
        System.out.print("ステータスを変更するタスクコードを入力してください:");
        String taskCodeStr = reader.readLine();

        // タスクコードが数字かどうかをチェック
        if (!isNumeric(taskCodeStr)) {
            System.out.println("タスクコードは半角の数字で入力してください");
            return;
        }

        int taskCode = Integer.parseInt(taskCodeStr);
        // ステータスの選択
        System.out.println("1. 着手中, 2. 完了");
        System.out.print("選択肢：");
        String statusChoiceStr = reader.readLine();

        // ステータス選択が1または2であるかチェック
        if (!isNumeric(statusChoiceStr)) {
            System.out.println("ステータスは半角の数字で入力してください");
            return;
        }
        
        int statusChoice = Integer.parseInt(statusChoiceStr);
        if (statusChoice != 1 && statusChoice != 2) {
            System.out.println("ステータスは1・2の中から選択してください");
            return;
        }

        // ステータスの変更処理
        taskLogic.changeStatus(taskCode, statusChoice, loginUser);
        System.out.println("ステータスの変更が完了しました。");

    } catch (IOException e) {
        System.out.println("入力エラーが発生しました。再度お試しください。");
    } catch (AppException e) {
        System.out.println("エラー：" + e.getMessage());
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
        while (true) {
            try {
                System.out.print("タスクコードを入力してください：");
                String taskCodeStr = reader.readLine();
                if (!isNumeric(taskCodeStr)) {
                    System.out.println("コードは半角の数字で入力してください");
                    continue;
                }
                int taskCode = Integer.parseInt(taskCodeStr);

                System.out.print("タスク名を入力してください：");
                String taskName = reader.readLine();
                if (taskName.length() > 10) {
                    System.out.println("タスク名は10文字以内で入力してください");
                    continue;
                }

                System.out.print("担当するユーザーのコードを選択してください：");
                String userCodeStr = reader.readLine();
                if (!isNumeric(userCodeStr)) {
                    System.out.println("ユーザーのコードは半角の数字で入力してください");
                    continue;
                }
                int userCode = Integer.parseInt(userCodeStr);

                // タスク登録処理
                taskLogic.save(taskCode, taskName, userCode, loginUser);
                System.out.println(taskName + "の登録が完了しました。");
                break;
            } catch (IOException e) {
                System.out.println("入力エラーが発生しました。再度お試しください。");
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
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
        taskUI.displayMenu();
    }
}
