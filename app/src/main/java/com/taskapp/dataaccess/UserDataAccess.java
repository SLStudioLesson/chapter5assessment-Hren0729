package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.taskapp.model.User;

public class UserDataAccess {
    private final String filePath;

    public UserDataAccess() {
        filePath = "app\\src\\main\\resources\\users.csv"; // CSVファイルのパス
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     */
    public UserDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * メールアドレスとパスワードを基にユーザーデータを探します。
     * @param email メールアドレス
     * @param password パスワード
     * @return 見つかったユーザー
     */
    // UserDataAccessクラスでuser.csvの読み込みエラーがあった
    public User findByEmailAndPassword(String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            // 最初の行（ヘッダー行）を読み飛ばす
            br.readLine(); // ヘッダー行をスキップ
    
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",",-1);
    
                // データの長さが正しいか確認
                if (data.length == 4) {
                    // メールアドレスとパスワードが一致するかを確認
                    if (data[2].equals(email) && data[3].equals(password)) {
                        int code = Integer.parseInt(data[0]);
                        String userName = data[1];
                        return new User(code, email, password, userName);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("CSVファイルの読み込み中にエラーが発生しました: " + e.getMessage());
        }
    
        return null;  // ユーザーが見つからない場合
    }

    /**
     * コードを基にユーザーデータを取得します。
     * @param code 取得するユーザーのコード
     * @return 見つかったユーザー
     */
    public User findByCode(int code) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // ヘッダー行をスキップ
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length == 4 && Integer.parseInt(data[0]) == code) {
                    String email = data[1];
                    String password = data[2];
                    String userName = data[3];
                    return new User(code, email, password, userName);
                }
            }
        } catch (IOException e) {
            System.out.println("CSVファイルの読み込み中にエラーが発生しました: " + e.getMessage());
        }

        return null;
    }
}
