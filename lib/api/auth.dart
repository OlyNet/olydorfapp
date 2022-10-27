import 'dart:developer';

import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart' as appwriteModels;
import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/models/user_model.dart';

class AuthState extends StateNotifier<AppUser?> {
  final Client client;
  late Account account;
  late Databases databases;

  AuthState(this.client) : super(null) {
    account = Account(client);
    databases = Databases(client);
  }

  // get user and userdata from appwrite
  Future<void> getCurrentUser() async {
    try {
      final user = await account.get();
      final data = await databases.getDocument(
          databaseId: 'olydorf', collectionId: 'users', documentId: user.$id);
      Teams teams = Teams(client);
      appwriteModels.TeamList teamsList = await teams.list();
      state = AppUser.fromMap(data.data, teamsList.teams);
    } catch (e) {
      log(e.toString());
    }
  }

  // login email
  Future<void> login(
      String email, String password, BuildContext context) async {
    try {
      await account.createEmailSession(email: email, password: password);
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
      appwriteModels.Account newUser = await account.create(
        userId: 'unique()',
        email: email,
        password: password,
        name: name,
      );

      await account.createEmailSession(email: email, password: password);

      await addUser(newUser);

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

  Future<void> addUser(appwriteModels.Account newUser) async {
    try {
      await databases.createDocument(
          databaseId: 'olydorf',
          collectionId: 'users',
          documentId: newUser.$id,
          data: {
            'id': newUser.$id,
            'name': newUser.name,
            'email': newUser.email,
          },
          permissions: [
            Permission.read(Role.any()),
            Permission.update(Role.user(newUser.$id)),
          ]);
    } catch (e) {
      log(e.toString());
    }
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
