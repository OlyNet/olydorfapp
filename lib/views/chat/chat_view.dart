import 'dart:convert';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_chat_ui/flutter_chat_ui.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:flutter_chat_types/flutter_chat_types.dart' as types;

// For the testing purposes, you should probably use https://pub.dev/packages/uuid
String randomString() {
  final random = Random.secure();
  final values = List<int>.generate(16, (i) => random.nextInt(255));
  return base64UrlEncode(values);
}

class ChatView extends StatefulHookConsumerWidget {
  const ChatView({Key? key}) : super(key: key);

  @override
  _ChatViewState createState() => _ChatViewState();
}

class _ChatViewState extends ConsumerState<ChatView> {
  List<types.Message> _messages = [];

  final _user = const types.User(id: '123');

  void _addMessage(types.Message message) {
    setState(() {
      _messages.insert(0, message);
    });
  }

  void _handleSendPressed(types.PartialText message) {
    final textMessage = types.TextMessage(
      author: _user,
      createdAt: DateTime.now().millisecondsSinceEpoch,
      id: randomString(),
      text: message.text,
    );

    _addMessage(textMessage);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('General chat'),
      ),
      body: SafeArea(
        bottom: false,
        child: Chat(
          theme: const DefaultChatTheme(primaryColor: Colors.blue),
          messages: _messages,
          onSendPressed: _handleSendPressed,
          user: _user,
        ),
      ),
    );
  }
}
