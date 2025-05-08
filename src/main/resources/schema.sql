create table if not exists shopping_history (
    id serial primary key,
    purchased_date date not null,
    purchased_item varchar not null,
    payment_amount integer not null
);