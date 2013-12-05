/*
 * NativeBridgeHelper.cpp
 *
 *  Created on: 18 juil. 2013
 *      Author: sarlhaploid
 */

#include "NativeBridgeHelper.hpp"

#include <qdebug.h>
#include <qfile.h>
#include <qtextstream.h>

using namespace bb::device;

NativeBridgeHelper::NativeBridgeHelper() {

}

NativeBridgeHelper::~NativeBridgeHelper() {

}

QByteArray NativeBridgeHelper::fromUnicodeToLocale(const QString &str) {

	return QTextCodec::codecForLocale()->fromUnicode(str);
}

QString NativeBridgeHelper::getAssetsFileContent(QString fileName) {

	QString filePath = "app/native/assets/" + fileName;

	QFile file(filePath);
	if (file.exists()) {
		if (file.open(QIODevice::ReadOnly | QIODevice::Text)) {
			QTextStream textStream(&file);
			QString text = textStream.readAll();
			file.close();
			return text;
		}
	} else {
		QString errorMsg("ERROR in getAssetsFileContent : File doesn't exist " + fileName);
		qDebug() << errorMsg;
	}

	return "";
}

QVariantMap NativeBridgeHelper::getScreenSize() {

	DisplayInfo display;

	QVariantMap q;
	q.insert("width", display.pixelSize().width());
	q.insert("height", display.pixelSize().height());

	return q;
}

