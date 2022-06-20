import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
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
              ElevatedButton(
                  onPressed: () {
                    if (!_formKey.currentState!.validate()) {
                      return;
                    }
                    Event event = Event(
                        name: eventNameController.text,
                        location: locationController.text,
                        description: descriptionController.text,
                        date: date);
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
