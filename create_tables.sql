drop table post;
create table post(
    id bigserial primary key,
    name varchar(250) not null,
    picture bytea,
    post_text text,
    tags varchar(4000),
    likes_count integer default 0);
drop table comment;
create table comment(
    id bigserial primary key,
    comment_text text,
    post_id bigint,
    foreign key (post_id) references post (id) on delete cascade);