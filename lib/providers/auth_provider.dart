import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/api/auth.dart';
import 'package:olydorf/api/user_data.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/models/user_model.dart';

final authProvider = Provider<Auth>((ref) {
  return Auth(ref.watch(clientProvider));
});

final clientProvider = Provider<Client>((ref) {
  return Client()
      .setEndpoint(AppwriteConfig.endpointUrl)
      .setProject(AppwriteConfig.projectId)
      .setSelfSigned(
          status: true); // TODO only in development (self signed certificate)
});

final userProvider = FutureProvider<User?>((ref) async {
  return ref.watch(authProvider).getAccount();
});

final userDataClassProvider = Provider<UserData>((ref) {
  return UserData(ref.watch(clientProvider));
});

final userLoggedInProvider = StateProvider<bool?>((ref) {
  return null; // null for loading screen
});

final currentUserProvider = StateProvider<AppUser?>((ref) {
  return null;
});
