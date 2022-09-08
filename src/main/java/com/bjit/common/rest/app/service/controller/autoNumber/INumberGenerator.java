package com.bjit.common.rest.app.service.controller.autoNumber;

import matrix.db.Context;

public interface INumberGenerator {
    NumberGeneratorResponse generateAutonumber(NumberGenerationModel numberGenerationModel);
}
