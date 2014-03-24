/* 敵からの取得アイテム */
create table drop_items(
	id int primary key,
	name varchar(255),
	require_level int,
	enemy_name varchar(255));
create index on drop_items(name);
create index on drop_items(require_level);
/* 採取アイテム */
create table gathering_items(
	id int primary key,
	name varchar(255),
	require_level int,
	require_gatherer_class varchar(255));
create index on gathering_items(name);
create index on gathering_items(require_level);
/* 場所 */
create table spots(
	id int primary key,
	name varchar(255),
	x int,
	y int);
/* 敵からの取得アイテムと場所の関連 */
create table drop_item_spot_relations(
	drop_item_id int,
	spot_id int,
	primary key(drop_item_id, spot_id));
/* 採取アイテムと場所の関連 */
create table gathering_item_spot_relations(
	gathering_item_id int,
	spot_id int,
	primary key(gathering_item_id, spot_id));
/* サンプルデータ */
insert into drop_items values
(1, 'アプカレの卵', 16, 'アプカレ'),
(2, 'ダイアマイクの腱', 25, 'ベーンマイク'),
(3, 'ペイスタの粗皮', 49, 'バジリスタ');
insert into gathering_items values
(1, '鉄鋼', 11, '採掘師'),
(2, 'アップル', 41, '園芸師'),
(3, 'ブローフィッシュ', 22, '釣り師');
insert into spots values
(1, '東部森林', 13, 20),
(2, '鉱山前', 16, 2),
(3, '南部平原', 30, 25),
(4, '砂漠', 21, 19);
insert into drop_item_spot_relations values
(1, 1),
(1, 3),
(2, 1),
(3, 2),
(3, 4);
insert into gathering_item_spot_relations values
(1, 2),
(2, 1),
(2, 3),
(3, 1);

