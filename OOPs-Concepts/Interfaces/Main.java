// Definition:
// An interface is a contract that specifies WHAT methods a class must have,
// without saying HOW they work. A class that "implements" an interface
// must provide the actual code for every method the interface declares.

// Real-world use case:
// Food delivery / e-commerce apps need to notify users through different
// channels (SMS, Email, Push). Instead of writing separate logic wherever
// a notification is sent, the app codes against the Notifiable interface.
// Any new channel (e.g. WhatsApp) can be added later just by implementing
// Notifiable, with no change needed to the code that calls sendNotification().

/* 

"Interface = What to do, not How to do it."
Class = the worker. Interface = the job description.


==========================
When You Use It
==========================
- Unrelated classes need to share a behavior — e.g., Employee and Contractor aren't related by inheritance, but both can be Payable.
- You want multiple behaviors on one class — Java doesn't allow extending two classes, but you can implements many interfaces.
- You want to swap implementations easily — e.g., switch a payment gateway, a database, or a notification method without changing the rest of your code.
- You're designing for a plug-and-play system — like drivers, plugins, or callback handlers.

*/

interface Notifiable {
    void sendNotification(String message);
}

class SMSNotification implements Notifiable {
    public void sendNotification(String message) {
        System.out.println("SMS: " + message);
    }
}

class EmailNotification implements Notifiable {
    public void sendNotification(String message) {
        System.out.println("Email: " + message);
    }
}

public class Main {
    public static void main(String[] args) {
        Notifiable[] channels = { new SMSNotification(), new EmailNotification() };

        for (Notifiable channel : channels) {
            channel.sendNotification("Your order has shipped!");
        }
    }
}
