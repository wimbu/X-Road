/**
 * The MIT License
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.niis.xroad.restapi.repository;

import ee.ria.xroad.common.conf.serverconf.dao.ClientDAOImpl;
import ee.ria.xroad.common.conf.serverconf.model.ClientType;
import ee.ria.xroad.common.conf.serverconf.model.EndpointType;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.niis.xroad.restapi.service.ClientNotFoundException;
import org.niis.xroad.restapi.service.EndpointService;
import org.niis.xroad.restapi.service.NotFoundException;
import org.niis.xroad.restapi.util.PersistenceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@Transactional
public class EndpointRepository {

    private final PersistenceUtils persistenceUtils;

    public EndpointRepository(PersistenceUtils persistenceUtils) {
        this.persistenceUtils = persistenceUtils;
    }

    /**
     * Get Endpoint by id
     *
     * @param id
     * @return
     */
    public EndpointType getEndpoint(String id) {
        return this.persistenceUtils.getCurrentSession().get(EndpointType.class, Long.valueOf(id));
    }

    /**
     * Delete endpoint
     *
     * @param id
     * @throws EndpointService.EndpointNotFoundException
     */
    public void delete(String id) throws ClientNotFoundException, EndpointService.EndpointNotFoundException {
        Session session = this.persistenceUtils.getCurrentSession();
        EndpointType endpointType = session.get(EndpointType.class, Long.valueOf(id));

        if (endpointType == null) {
            throw new EndpointService.EndpointNotFoundException(id);
        }

        ClientDAOImpl clientDAO = new ClientDAOImpl();
        ClientType clientType = clientDAO.getClientByEndpointId(session, endpointType);

        if (clientType == null) {
            throw new ClientNotFoundException("Client not found for the given endpoint id: " + id);
        }

//        clientType.getAcl().forEach(acl -> {
//            if (acl.getEndpoint().getId().equals(id)) {
//                session.delete(acl);
//            }
//        });
        session.delete(endpointType);
    }

    /**
     * Executes a Hibernate saveOrUpdate({@Link EndpointType})
     *
     * @param endpointType
     */
    public void saveOrUpdate(EndpointType endpointType) {
        persistenceUtils.getCurrentSession().saveOrUpdate(endpointType);
    }


}
