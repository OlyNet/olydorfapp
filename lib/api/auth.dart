import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart';
import 'package:flutter/material.dart';
import 'package:olydorf/api/user_data.dart';
import 'package:olydorf/global/consts.dart';

class Auth {
  final Client client;
  late Account account;
  late Database database;

  Auth(this.client) {
    account = Account(client);
    database = Database(client);
  }

  // get User from appwrite
  Future<User?> getAccount() async {
    try {
      return await account.get();
    } on AppwriteException catch (e) {
      return null;
    }
  }

  // login email
  Future<void> login(
      String email, String password, BuildContext context) async {
    try {
      await account.createSession(email: email, password: password);
      await Navigator.pushReplacementNamed(context, Routes.bottomNavigationBar);
    } catch (e) {
      showErrorDialog(context, e);
    }
  }

  // sign up email
  Future<void> signUp(String email, String password, String name,
      UserData userData, BuildContext context) async {
    try {
      await account.create(
        userId: 'unique()',
        email: email,
        password: password,
        name: name,
      );

      await account.createSession(email: email, password: password);

      await addUser();

      await Navigator.pushReplacementNamed(context, Routes.bottomNavigationBar);
    } catch (e) {
      showErrorDialog(context, e);
    }
  }

  Future<void> addUser() async {
    User res = await account.get();

    print(res.$id);

    try {
      await database
          .createDocument(collectionId: 'users', documentId: res.$id, data: {
        'id': res.$id,
        'name': res.name,
        'email': res.email,
      }, read: [
        'role:all',
        'user:${res.$id}'
      ]);
    } catch (_) {}
  }

  // logout current session
  Future<void> logout(BuildContext context) async {
    try {
      await account.deleteSession(sessionId: 'current');
      await Navigator.of(context)
          .pushReplacementNamed(Routes.bottomNavigationBar);
    } catch (e) {
      showErrorDialog(context, e);
    }
  }

  void showErrorDialog(BuildContext context, Object error) {
    showDialog(
        context: context,
        builder: (BuildContext context) => AlertDialog(
              title: const Text('Error'),
              content: Text(error.toString()),
              actions: [
                TextButton(
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                    child: const Text("Ok"))
              ],
            ));
  }
}
