/* �G����̎擾�A�C�e�� */
create table drop_items(
	id int primary key,
	name varchar(255),
	require_level int,
	enemy_name varchar(255));
create index on drop_items(name);
create index on drop_items(require_level);
/* �̎�A�C�e�� */
create table gathering_items(
	id int primary key,
	name varchar(255),
	require_level int,
	require_gatherer_class varchar(255));
create index on gathering_items(name);
create index on gathering_items(require_level);
/* �ꏊ */
create table spots(
	id int primary key,
	name varchar(255),
	x int,
	y int);
/* �G����̎擾�A�C�e���Əꏊ�̊֘A */
create table drop_item_spot_relations(
	drop_item_id int,
	spot_id int,
	primary key(drop_item_id, spot_id));
/* �̎�A�C�e���Əꏊ�̊֘A */
create table gathering_item_spot_relations(
	gathering_item_id int,
	spot_id int,
	primary key(gathering_item_id, spot_id));
/* �T���v���f�[�^ */
insert into drop_items values
(1, '�A�v�J���̗�', 16, '�A�v�J��'),
(2, '�_�C�A�}�C�N���F', 25, '�x�[���}�C�N'),
(3, '�y�C�X�^�̑e��', 49, '�o�W���X�^');
insert into gathering_items values
(1, '�S�|', 11, '�̌@�t'),
(2, '�A�b�v��', 41, '���|�t'),
(3, '�u���[�t�B�b�V��', 22, '�ނ�t');
insert into spots values
(1, '�����X��', 13, 20),
(2, '�z�R�O', 16, 2),
(3, '�암����', 30, 25),
(4, '����', 21, 19);
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

