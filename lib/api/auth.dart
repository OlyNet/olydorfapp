import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart';
import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/models/user_model.dart';

class AuthState extends StateNotifier<AppUser?> {
  final Client client;
  late Account account;
  late Database database;

  AuthState(this.client) : super(null) {
    account = Account(client);
    database = Database(client);
  }

  // get user and userdata from appwrite
  Future<void> getCurrentUser() async {
    try {
      final user = await account.get();
      final data = await database.getDocument(
          collectionId: 'users', documentId: user.$id);
      Teams teams = Teams(client);
      TeamList teamsList = await teams.list();
      state = AppUser.fromMap(data.data, teamsList.teams);
    } catch (_) {}
  }

  // login email
  Future<void> login(
      String email, String password, BuildContext context) async {
    try {
      await account.createSession(email: email, password: password);
      await getCurrentUser();
      Navigator.pushNamedAndRemoveUntil(
          context, Routes.bottomNavigationBar, (route) => false);
    } catch (e) {
      showErrorDialog(context, e);
    }
  }

  // sign up email
  Future<void> signUp(
      String email, String password, String name, BuildContext context) async {
    try {
      await account.create(
        userId: 'unique()',
        email: email,
        password: password,
        name: name,
      );

      await account.createSession(email: email, password: password);

      await addUser();

      await getCurrentUser();

      Navigator.pushNamedAndRemoveUntil(
          context, Routes.bottomNavigationBar, (route) => false);
    } catch (e) {
      showErrorDialog(context, e);
    }
  }

  // login google oauth
  Future<void> loginGoogle(BuildContext context) async {
    try {
      await account.createOAuth2Session(
        provider: 'google',
      );
      await getCurrentUser();
      Navigator.pushNamedAndRemoveUntil(
          context, Routes.bottomNavigationBar, (route) => false);
    } catch (e) {
      showErrorDialog(context, e);
    }
  }

  Future<void> addUser() async {
    User res = await account.get();

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
      state = null;
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
