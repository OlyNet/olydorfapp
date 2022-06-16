import 'package:flutter/material.dart';

import 'club.dart';
import 'clubs_expantion_panel_list.dart';

//Item for showing in the list
class Item {
  Item({
    required this.expandedValue,
    required this.headerValue,
    this.isExpanded = false,
  });

  String expandedValue;
  String headerValue;
  bool isExpanded;
}

//Generating a dummy list
List<Club> clubs = List<Club>.generate(
    9,
    (counter) => Club("Club $counter", [
          WorkingHours(Day.Th, Time(18, 00), Time(19, 00)),
          WorkingHours(Day.Fr, Time(18, 00), Time(19, 30))
        ], [
          Contact("Bla bla", "blabla@test.de"),
          Contact("Na na", "nana@test.de")
        ]));

List<Item> makeItemList(List<Club> clubs) {
  List<Item> items = [];
  for (var club in clubs) {
    items.add(Item(
      headerValue: club.name,
      expandedValue: 'Working hours: ' +
          club.workingHours.toString() +
          "\n" +
          "Contacts: " +
          club.contacts.toString(),
    ));
  }
  return items;
}

List<Item> items = makeItemList(clubs);

class ClubsStatefulWidget extends StatefulWidget {
  const ClubsStatefulWidget({Key? key}) : super(key: key);

  @override
  State<ClubsStatefulWidget> createState() => _ClubsStatefulWidgetState();
}

class _ClubsStatefulWidgetState extends State<ClubsStatefulWidget> {
  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
        child: SizedBox(height: 800, child: _buildPanel()));
  }

  Widget _buildPanel() {
    return ClubsExpansionPanelList(
      expansionCallback: (int index, bool isExpanded) {
        setState(() {
          items[index].isExpanded = !isExpanded;
        });
      },
      children: items.map<ExpansionPanel>((Item item) {
        return ExpansionPanel(
          headerBuilder: (BuildContext context, bool isExpanded) {
            return ListTile(
              title: Text(item.headerValue),
            );
          },
          body: ListTile(
            title: Text(item.expandedValue),
            onTap: () {
              setState(() {
                items.removeWhere((Item currentItem) => item == currentItem);
              });
            },
          ),
          isExpanded: item.isExpanded,
        );
      }).toList(),
    );
  }
}
