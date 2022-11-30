import 'package:flutter/material.dart';
import 'package:olydorf/models/event_model.dart';

class EventCard extends StatelessWidget {
  late Event event;
  void Function()? onPressed;
  EventCard({
    Key? key,
    required this.event,
    this.onPressed,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      clipBehavior: Clip.antiAlias,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(20),
      ),
      child: InkWell(
        onTap: onPressed,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            if (event.image != null) ...[
              Image.memory(event.image!),
            ],
            Padding(
              padding: const EdgeInsets.all(28),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(event.name,
                      style: Theme.of(context).textTheme.headline5),
                  if (event.location != null) ...[
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          const Icon(Icons.place),
                          Text(
                            event.location!,
                            style: const TextStyle(fontSize: 16),
                          ),
                        ],
                      ),
                    ),
                  ],
                  if (event.date != null) ...[
                    Center(
                      child: Text(
                          "${event.date!.day.toString().padLeft(2, '0')} / ${event.date!.month.toString().padLeft(2, '0')} / ${event.date!.year.toString()}"),
                    )
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
