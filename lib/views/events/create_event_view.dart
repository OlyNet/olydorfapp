import 'dart:developer';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:image_picker/image_picker.dart';
import 'package:olydorf/models/event_model.dart';
import 'package:intl/intl.dart';

import 'package:olydorf/providers/events_provider.dart';

class CreateEventView extends StatefulHookConsumerWidget {
  const CreateEventView({Key? key}) : super(key: key);

  @override
  ConsumerState<CreateEventView> createState() => _CreateEventViewState();
}

class _CreateEventViewState extends ConsumerState<CreateEventView> {
  final _formKey = GlobalKey<FormState>();

  final eventNameController = TextEditingController();

  final descriptionController = TextEditingController();

  final locationController = TextEditingController();

  DateTime date = DateTime.now();
  TimeOfDay time = TimeOfDay.now();

  final ImagePicker _picker = ImagePicker();
  XFile? _image;

  void pickDate(BuildContext context) async {
    date = await showDatePicker(
            context: context,
            initialDate: DateTime.now(),
            firstDate: DateTime.now(),
            lastDate: DateTime.now().add(const Duration(days: 300))) ??
        DateTime.now();

    setState(() {});
  }

  void pickTime(BuildContext context) async {
    time =
        await showTimePicker(context: context, initialTime: TimeOfDay.now()) ??
            TimeOfDay.now();
    setState(() {});
  }

  Future<void> pickImage(ImagePicker picker) async {
    final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
    if (image != null) {
      setState(() {
        _image = image;
      });
      log(_image!.path);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Create Event"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: SingleChildScrollView(
          child: Form(
            key: _formKey,
            child: Column(children: [
              const SizedBox(
                height: 20,
              ),
              TextFormField(
                controller: eventNameController,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter a name';
                  }
                  return null;
                },
                decoration: const InputDecoration(
                  labelText: "Event Name",
                  hintText: "e.g. BBQ",
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(
                height: 20,
              ),
              TextFormField(
                controller: locationController,
                decoration: const InputDecoration(
                  labelText: "Location",
                  hintText: "e.g. Marienplatz",
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(
                height: 20,
              ),
              TextFormField(
                controller: descriptionController,
                decoration: const InputDecoration(
                  labelText: "Description",
                  hintText: "...",
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(
                height: 20,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  Row(
                    children: [
                      const Text("Date: "),
                      TextButton(
                          onPressed: () {
                            pickDate(context);
                          },
                          child: Text(DateFormat("dd.MM.yyyy").format(date))),
                    ],
                  ),
                  Row(
                    children: [
                      const Text("Time: "),
                      TextButton(
                          onPressed: () {
                            pickTime(context);
                          },
                          child: Text(time.format(context))),
                    ],
                  )
                ],
              ),
              InkWell(
                  borderRadius: BorderRadius.circular(50),
                  enableFeedback: true,
                  onTap: () => pickImage(_picker),
                  child: CircleAvatar(
                      radius: 56,
                      child: CircleAvatar(
                        radius: 52,
                        backgroundImage: _image == null
                            ? null
                            : FileImage(File(_image!.path)),
                      ))),
              ElevatedButton(
                  onPressed: () async {
                    if (!_formKey.currentState!.validate()) {
                      return;
                    }
                    String? imgId;
                    log(_image.toString());
                    if (_image != null) {
                      imgId = await ref
                          .read(eventsListProvider.notifier)
                          .uploadEventPicture(_image!.path, _image!.name);
                    }
                    log(imgId.toString());
                    Event event = Event(
                        name: eventNameController.text,
                        location: locationController.text,
                        description: descriptionController.text,
                        date: date,
                        imgId: imgId);
                    ref.read(eventsListProvider.notifier).createEvent(event);
                    Navigator.of(context).pop();
                  },
                  child: const Text("create")),
            ]),
          ),
        ),
      ),
    );
  }
}
