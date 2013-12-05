/*
 * NativeBridgeHelper.h
 *
 *  Created on: 18 juil. 2013
 *      Author: sarlhaploid
 */

#ifndef NATIVEBRIDGEHELPER_H_
#define NATIVEBRIDGEHELPER_H_

#include <QObject>
#include <bb/device/DisplayInfo>

class NativeBridgeHelper: public QObject {

	Q_OBJECT

	public:

		NativeBridgeHelper();
		virtual ~NativeBridgeHelper();

	public:

		// Used to share text with correct encoding
		// See: http://supportforums.blackberry.com/t5/Cascades-Development/Share-invocation-and-the-pound-sign/m-p/2380147/highlight/true#M22536
		Q_INVOKABLE static QByteArray fromUnicodeToLocale(const QString &str);
		Q_INVOKABLE static QString getAssetsFileContent(QString fileName);
		Q_INVOKABLE static QVariantMap getScreenSize();
};

#endif /* NATIVEBRIDGEHELPER_H_ */
