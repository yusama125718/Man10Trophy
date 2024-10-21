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
* [Man10Trophy] トロフィー削除 <id>
  * done
* [Man10TrophyEdit] 新規作成 <id>
  * done
* [Man10TrophyEdit] アイテム編集 <display/cost/item> <id>
  * delete => done
  * cost => done
  * item => done

## その他処理

* 右クリック時編集コマンド表示処理
  * done

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

## Command

* /mtro : トロフィー交換メニューを開く
  * done
* /mtro title [タイトル] : 手に持っているトロフィーのアイテム名を編集（自身が発行した物のみ）
  * done
* /mtro lore [行番号] [内容] : 手に持っているトロフィーの説明を編集。:blankで空行を設定（自身が発行した物のみ）
  * done
* /mtro [on/off] : システムを[起動/停止]
  * done
* /mtro create [内部名] : トロフィー追加メニューを開く
  * done
* /mtro edit : トロフィー編集メニューを開く
  * done
* /mtro editor [display/item/cost] [id] [内容] : トロフィーのアイテム名を編集
  * display
    * done
  * item
    * done
  * cost
    * done
* /mtro editor lore [display/item/cost] [id] [行] [内容] : トロフィーの説明を編集
  * display
    * done
  * item
    * done
  * cost
    * done
* /mtro editor score [id] [score] : 要求スコアを設定
  * done

## TODO

* スコア判定処理を追加
  * done
* テスト仕様書