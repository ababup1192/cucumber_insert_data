# encoding: utf-8

Feature: テストデータ

  Scenario: つぶやきがないユーザを作る
    Given DBの設定をする
    When id 1 name "John" のユーザを作る
    Then コネクションを閉じる

  Scenario: つぶやきを持つユーザを作る
    Given DBの設定をする
    When id 2 name "Mike" のユーザを作る
    And つぶやきを作る
      | user_id | content |
      | 2       | hello   |
      | 2       | world   |
      | 2       | hoge    |
    Then コネクションを閉じる

  Scenario: パフォーマンステスト用に、つぶやきが多いユーザを作る
    Given DBの設定をする
    When id 3 name "God" のユーザを作る
    And user_id 3 のユーザに ランダムな内容1000個のつぶやきを作る
    Then コネクションを閉じる


