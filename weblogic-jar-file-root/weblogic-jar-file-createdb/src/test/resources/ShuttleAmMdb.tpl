/*
 * -----------------------------------------------------------------------------
 * Application     : WM 6
 * Revision        : \$Revision: 356571 \$
 * Revision date   : \$Date: 2015-10-22 10:59:45 +0200 (Thu, 22 Oct 2015) \$
 * Last changed by : \$Author: b7godin \$
 * URL             : \$URL: http://almscdc.swisslog.com/repo/SWPD/Development/WM6/trunk/Software/WM6/wm6-techlab-project/wm6-techlab-components/wm6-techlab-main/src/main/java/com/swisslog/wm6/techlab/impl/transport/mdb/LGCS11AmMdb.java \$
 * 
 * -----------------------------------------------------------------------------
 * Copyright
 * This software module including the design and software principals used
 * is and remains the property of Swisslog and is submitted with the
 * understanding that it is not to be reproduced nor copied in whole or in
 * part, nor licensed or otherwise provided or communicated to any third
 * party without Swisslog's prior written consent.
 * It must not be used in any way detrimental to the interests of Swisslog.
 * Acceptance of this module will be construed as an agreement to the above.
 *
 * All rights of Swisslog remain reserved. Swisslog and WarehouseManager
 * are trademarks or registered trademarks of Swisslog. Other products
 * and company names mentioned herein may be trademarks or trade names of
 * their respective owners. Specifications are subject to change without
 * notice.
 * -----------------------------------------------------------------------------
 */
 
 /*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package entrypoint.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.interceptor.Interceptors;

import inteceptor.MdbInterceptor;

/**
 * All the logic of loggging message received and tracking execution time is in the parent this is just a shell class to
 * bind itself to a queue.
 */
@MessageDriven(name="${mdbName}", mappedName = "queue/${queueName}", activationConfig = {
        @ActivationConfigProperty(propertyName = "endpointExceptionRedeliveryAttempts", propertyValue = "2"),
        @ActivationConfigProperty(propertyName = "endpointExceptionRedeliveryInterval", propertyValue = "200") })
@Interceptors(MdbInterceptor.class)
public class ${mdbName} extends AbstractMdb {

}
 
