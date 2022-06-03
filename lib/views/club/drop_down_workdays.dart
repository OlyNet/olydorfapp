import 'package:flutter/material.dart';

List<DropdownMenuItem<String>> get dropdownItems {
  List<DropdownMenuItem<String>> menuItems = [
    const DropdownMenuItem(child: Text("Monday"), value: "Mo"),
    const DropdownMenuItem(child: Text("Tuesday"), value: "Tu"),
    const DropdownMenuItem(child: Text("Wednesday"), value: "We"),
    const DropdownMenuItem(child: Text("Thursday"), value: "Th"),
    const DropdownMenuItem(child: Text("Friday"), value: "Fr"),
    const DropdownMenuItem(child: Text("Saturday"), value: "Sa"),
    const DropdownMenuItem(child: Text("Sunday"), value: "Su"),
  ];
  return menuItems;
}

class DropdownItem extends StatefulWidget {
  const DropdownItem({Key? key}) : super(key: key);

  @override
  State<DropdownItem> createState() => _DropdownItemState();
}

class _DropdownItemState extends State<DropdownItem> {
  String selectedValue = "Mo";
  void setSelectedValue(String str) {}

  @override
  Widget build(BuildContext context) {
    return DropdownButton(
        value: selectedValue,
        onChanged: (String? newValue) {
          setState(() {
            selectedValue = newValue!;
            setSelectedValue(selectedValue);
          });
        },
        items: dropdownItems);
  }
}
