# 仕様

* アイテムとの交換で入手する
* 交換したアイテムにNBTでUUIDを仕込む
* アイテム右クリ時にNBTのUUIDと一致したら編集コマンド一覧を送信
* [Man10Trophy]はアイテム移動不可、[Man10TrophyEdit]はスロットを見てキャンセルか決める
* トロフィーのlore設定は:blankで空行になる

## GUI

* [Man10Trophy] 編集メニュー <page>
  * done
* [Man10Trophy] メインメニュー <page>
  * done
* [Man10Trophy] 交換画面 <id>
  * done
* [Man10Trophy] 編集画面 <id>
  * done
* [Man10TrophyEdit] 新規作成 <id>
* [Man10TrophyEdit] アイテム編集 <display/cost/item> <id>

## その他処理

* 右クリック時編集コマンド表示処理

## トロフィーデータ

* item
  * 交換先アイテム
* cost
  * 交換元アイテム
* display
  * 一覧用アイテム
* name
  * 内部名(ファイル名)
* score
  * 必要スコア
* state
  * 販売状態

## DB

* id
* time
* trophy_name
* mcid
* uuid