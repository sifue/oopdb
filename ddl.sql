/* 番組 */
create table programs(
	id int primary key,
	title varchar(255),
	begin_time timestamp,
	end_time timestamp);
create index on programs(title);
alter table programs add constraint title_unique unique(title);

/* タグ */
create table tags(
	id int primary key,
	name varchar(255),
	is_category boolean);

/* 番組とタグの関連 */
create table program_tag_relations(
	program_id int,
	tag_id int,
	primary key(program_id, tag_id));
create index on program_tag_relations(program_id);
create index on program_tag_relations(tag_id);

/* サンプルデータ */
insert into programs values
(1, '第3回電王戦第5局', timestamp '2014-03-28 09:30:00', timestamp '2014-03-28 19:30:00'),
(2, '衆議院国会生中継', timestamp '2014-03-28 13:00:00', timestamp '2014-03-28 17:00:00'),
(3, 'ニコラジ', timestamp '2014-03-28 22:00:00', timestamp '2014-03-28 23:00:00');
insert into tags values
(1, '公式放送', true),
(2, '政治', false),
(3, '歌ってみた', false),
(4, '将棋', false);
insert into program_tag_relations values
(1, 1),
(1, 2),
(1, 4),
(2, 1),
(2, 2),
(3, 1),
(3, 3);
