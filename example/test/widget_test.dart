// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:widget_recorder_example/main.dart';

void main() {
  // final data = convertToTelex("Tôi là ai giữa cuộc đời này");
  // print("data is  ${json.encode(data)}");
  test('Converts Vietnamese string to Unikey Telex format', () {
    String vietnameseString = "Tôi là ai giữa cuộc đời này";
    List<String> expectedTelexList =  ["t","o","o","i"," ","l","a","f"," ","a","i"," ","g","i","u","w","x","a"," ","c","u","o","o","j","c"," ","d","d","o","w","f","i"," ","n","a","f","y"];

    List<String> telexList = convertToTelex(vietnameseString);
    expect(telexList, expectedTelexList);
  });
}

List<String> convertToTelex(String vietnameseString) {
  Map<String, String> telexMapping = {
    'a': 'a', 'á': 'as', 'à': 'af', 'ả': 'ar', 'ã': 'ax', 'ạ': 'aj',
    'ă': 'aw', 'ắ': 'aws', 'ằ': 'awf', 'ẳ': 'awr', 'ẵ': 'awx', 'ặ': 'awj',
    'â': 'aa', 'ấ': 'aas', 'ầ': 'aaf', 'ẩ': 'aar', 'ẫ': 'aax', 'ậ': 'aaj',
    'b': 'b', 'c': 'c', 'd': 'd', 'đ': 'dd',
    'e': 'e', 'é': 'es', 'è': 'ef', 'ẻ': 'er', 'ẽ': 'ex', 'ẹ': 'ej',
    'ê': 'ee', 'ế': 'ees', 'ề': 'eef', 'ể': 'eer', 'ễ': 'eex', 'ệ': 'eej',
    'f': 'f', 'g': 'g', 'h': 'h', 'i': 'i', 'í': 'is', 'ì': 'if', 'ỉ': 'ir',
    'ĩ': 'ix', 'ị': 'ij', 'j': 'j', 'k': 'k', 'l': 'l', 'm': 'm', 'n': 'n',
    'o': 'o', 'ó': 'os', 'ò': 'of', 'ỏ': 'or', 'õ': 'ox', 'ọ': 'oj',
    'ô': 'oo', 'ố': 'oos', 'ồ': 'oof', 'ổ': 'oor', 'ỗ': 'oox', 'ộ': 'ooj',
    'ơ': 'ow', 'ớ': 'ows', 'ờ': 'owf', 'ở': 'owr', 'ỡ': 'owx', 'ợ': 'owj',
    'p': 'p', 'q': 'q', 'r': 'r', 's': 's', 't': 't', 'u': 'u', 'ú': 'us',
    'ù': 'uf', 'ủ': 'ur', 'ũ': 'ux', 'ụ': 'uj', 'ư': 'uw', 'ứ': 'uws',
    'ừ': 'uwf', 'ử': 'uwr', 'ữ': 'uwx', 'ự': 'uwj', 'v': 'v', 'x': 'x',
    'y': 'y', 'ý': 'ys', 'ỳ': 'yf', 'ỷ': 'yr', 'ỹ': 'yx', 'ỵ': 'yj',
    ' ': ' '
  };

  List<String> telexList = [];
  for (var i = 0; i < vietnameseString.length; i++) {
    var char = vietnameseString[i];
    var nextChar = (i < vietnameseString.length - 1) ? vietnameseString[i + 1] : '';
    var charKey = char.toLowerCase();
    final telexChar = telexMapping[charKey];
    if (telexChar != null) {
      if (telexChar.length > 1) {
        if (nextChar == '̣' || nextChar == '̉' || nextChar == '̃' || nextChar == '̣' || nextChar == '́') {
          telexList.add(telexChar.substring(0, telexChar.length - 1) + nextChar);
          i++;
        } else {
          final text = convertToTelex(telexChar);
          telexList.addAll(text);
        }
      } else {
        telexList.add(telexChar);
      }
    } else {
      telexList.add(char);
    }
  }

  return telexList;
}