import 'package:hooks_riverpod/hooks_riverpod.dart';

class Club {
  String name;
  List<WorkingHours> workingHours;
  List<Contact> contacts;

  Club(this.name, this.workingHours, this.contacts);
}

class Contact {
  String name;
  String email;

  Contact(this.name, this.email);

  @override
  String toString() {
    return name + ': ' + email;
  }
}

class WorkingHours {
  Day day;
  Time startingHour;
  Time finishingHour;

  WorkingHours(this.day, this.startingHour, this.finishingHour);

  @override
  String toString() {
    return day.name +
        ' ' +
        startingHour.toString() +
        ' - ' +
        finishingHour.toString();
  }
}

enum Day { Mo, Tu, We, Th, Fr, Sa, Su }

class Time {
  int hours = 0;
  int minutes = 0;

  Time(int hours, int minutes) {
    if (hours < 0) {
      this.hours = 0;
    } else if (hours > 23) {
      this.hours = 0;
    } else {
      this.hours = hours;
    }
    if (minutes < 0) {
      this.minutes = 0;
    } else if (minutes > 59) {
      this.minutes = 0;
    } else {
      this.minutes = minutes;
    }
  }

  @override
  String toString() {
    String hours = this.hours.toString();
    if (hours.length < 2) {
      hours = '0' + hours;
    }
    String minutes = this.minutes.toString();
    if (minutes.length < 2) {
      minutes = '0' + minutes;
    }
    return hours + ':' + minutes;
  }
}
