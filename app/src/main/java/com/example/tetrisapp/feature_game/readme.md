## 全体の流れ
## UI(View) → ViewModel → UseCase → Repository → データ層

## GameRepositoryImplのなかに処理のためのコードを大量に入れていって、usecaseで特定のものについてまとめてviewModelでuseCaseをさらに一つにまとめる
## まとめたものをuiに入れて出力する



@Previewとは異なる、Composableごとにテストが簡単にできるツールがあるみたい