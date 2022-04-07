import 'package:appwrite/appwrite.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/api/auth.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/models/user_model.dart';

final authProvider = StateNotifierProvider<AuthState, AppUser?>(
    (ref) => AuthState(ref.watch(clientProvider)));

final clientProvider = Provider<Client>((ref) {
  return Client()
      .setEndpoint(AppwriteConfig.endpointUrl)
      .setProject(AppwriteConfig.projectId);
});
