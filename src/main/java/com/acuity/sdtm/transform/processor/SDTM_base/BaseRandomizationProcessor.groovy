/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.sdtm.transform.processor.SDTM_base


import com.acuity.sdtm.transform.io.writer.Writer
import com.acuity.sdtm.transform.common.BaseEntityProcessor
import com.acuity.sdtm.transform.common.EntityType
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.conversions.Bson

import static com.mongodb.client.model.Filters.and
import static com.mongodb.client.model.Filters.eq

@Slf4j
public abstract class BaseRandomizationProcessor extends BaseEntityProcessor {
    def HEADER = ['STUDY', 'PART', 'SUBJECT', 'RAND_DATE']

    @Override
    public EntityType getEntityType() {
        return EntityType.Randomization
    }


    @Override
    Bson getDomainFilter() {
        return and(eq('DSCAT', 'PROTOCOL MILESTONE'), eq('DSTERM', 'RANDOMIZATION CODE ALLOCATED'), eq('DSDECOD', 'RANDOMIZATION CODE ALLOCATED'),)
    }

    @Override
    public void process(MongoDatabase mongo, Writer writer) throws IOException {
        MongoCollection<Document> ds = mongo.getCollection("${sessionId}_DS")

        try {
            writer.open(study, 'randomization', HEADER)

            ds.find(getDomainFilter()).each { row ->
                writer.write(map(row))
            }
        } finally {
            writer.close()
        }
    }
}
