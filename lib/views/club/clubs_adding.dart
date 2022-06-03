import 'package:flutter/material.dart';
import 'package:olydorf/common/club.dart';

import 'drop_down_workdays.dart';

class ClubsAddingStatefulWidget extends StatefulWidget {
  const ClubsAddingStatefulWidget({Key? key}) : super(key: key);

  @override
  State<ClubsAddingStatefulWidget> createState() =>
      _ClubsAddingStatefulWidgetState();
}

class _ClubsAddingStatefulWidgetState extends State<ClubsAddingStatefulWidget> {
  List<Club> clubs = [];
  //Contacts Variables
  List<Contact> contacts = <Contact>[];
  void addContact() {
    setState(() {
      contacts.insert(0, Contact(myController1.text, myController2.text));
    });
  }

  //Time and working hours variables
  TimeOfDay initialTime = TimeOfDay.now();
  TimeOfDay pickedTimeOpen = TimeOfDay.now();
  TimeOfDay pickedTimeClose = TimeOfDay.now();
  List<WorkingHours> workingHours = <WorkingHours>[];
  void addWorkingHours(String? day, TimeOfDay start, TimeOfDay close) {
    setState(() {
      day ??= "Mo";
      workingHours.insert(
          0,
          WorkingHours(
              Day.values.firstWhere((e) => e.toString() == 'Day.' + day!),
              Time(start.hour, start.minute),
              Time(close.hour, close.minute)));
    });
  }

  //Controlers for the text input
  final myController = TextEditingController();
  final myController1 = TextEditingController();
  final myController2 = TextEditingController();

  @override
  void dispose() {
    myController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(child: _build()),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Add',
        child: const Icon(Icons.add),
        onPressed: () {
          //ADDING THE CLUB INFO TO THE LIST
          Club club = Club(myController.text, workingHours, contacts);
          clubs.add(club);
          showDialog(
            context: context,
            builder: (context) {
              return const AlertDialog(
                content: Text('Club Added'),
              );
            },
          );
        },
      ),
    );
  }

  Widget _build() {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: <Widget>[
        //CLUB NAME
        Padding(
          padding: const EdgeInsets.all(20),
          child: TextField(
            controller: myController,
            decoration: const InputDecoration(
              border: OutlineInputBorder(),
              labelText: 'Club Name',
            ),
          ),
        ),
        //WORKING HOURS
        Padding(
            padding: const EdgeInsets.all(20),
            child: ListTile(
                title: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: <Widget>[
                  const DropdownItem(),
                  ElevatedButton(
                      onPressed: () async {
                        pickedTimeOpen = (await showTimePicker(
                          context: context,
                          initialTime: initialTime,
                        ))!;
                      },
                      child: const Text('Opening hours')),
                  ElevatedButton(
                      onPressed: () async {
                        pickedTimeClose = (await showTimePicker(
                          context: context,
                          initialTime: initialTime,
                        ))!;
                      },
                      child: const Text('Closing hours'))
                ]))),
        ElevatedButton(
          child: const Text('Add Time'),
          onPressed: () {
            addWorkingHours('Mo', pickedTimeOpen, pickedTimeClose);
          },
        ),
        SizedBox(
            height: 75,
            child: ListView.builder(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.all(8),
                itemCount: workingHours.length,
                itemBuilder: (BuildContext context, int index) {
                  return Container(
                    height: 25,
                    margin: const EdgeInsets.all(2),
                    color: Colors.blue[400],
                    child: Center(
                        child: Text(
                      '${workingHours[index].toString()}',
                    )),
                  );
                })),

        //CLUB CONTACTS
        Padding(
          padding: const EdgeInsets.all(20),
          child: TextField(
            controller: myController1,
            decoration: const InputDecoration(
              border: OutlineInputBorder(),
              labelText: 'Contact Name',
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(20),
          child: TextField(
            controller: myController2,
            decoration: const InputDecoration(
              border: OutlineInputBorder(),
              labelText: 'Contact E-mail',
            ),
          ),
        ),
        ElevatedButton(
          child: const Text('Add Contact'),
          onPressed: () {
            addContact();
          },
        ),
        SizedBox(
            height: 75,
            child: ListView.builder(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.all(8),
                itemCount: contacts.length,
                itemBuilder: (BuildContext context, int index) {
                  return Container(
                    height: 25,
                    margin: const EdgeInsets.all(2),
                    color: Colors.blue[400],
                    child: Center(
                        child: Text(
                      '${contacts[index].name} (${contacts[index].email})',
                    )),
                  );
                }))
      ],
    );
  }
}
