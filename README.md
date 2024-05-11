# Learn DE backend project

This is the backend part of a Learn DE Project - an online platform to learn german articles with ease.

This spring boot application has two environments: `test` and `prod`.
- `test` is used to run tests
- `prod` is used to run actual application

`test` environment uses hardcoded config for simplicity.

To run the `prod` environment one need to:
- set VM option `-Dspring.profiles.active=prod`
- add `JWT_SECRET` and `DBPATH` environment variables

This project uses `Sqlite` database.

To be able to add custom topics and words you need to have admin account. You need to create regular user account
and change the role in the database manually. Here is the example request to create user:
```
curl --location 'http://localhost:8080/api/v1/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Admin Admin",
    "username": "admin_username",
    "password": "IANDrYYtiOWp"
}'
```

Then connect to the sqlite database, look for the user in `user` table and set the role parameter as `ADMIN`.

Now you can log in and start using the app.

For better experience you can run the [ui application](https://github.com/aonufrei/learn-de-ui) also.