﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Services.Validation
{
    public class ValidationService
    {
        public bool CheckStringOnlyLetters(string input)
        {
            var regex = new Regex(@"^[a-zA-Z0-9\s]*$");

            return regex.IsMatch(input);
        }

        public bool CheckStringWithinLengthRange(int min, int max, string input)
        {
            return input.Length >= min && input.Length <= max;
        }

        public bool CheckDecimalWithinSizeRange(decimal min, decimal max, decimal input)
        {
            return input >= min && input <= max;
        }

        public bool CheckGuidValid(Guid guid)
        {
            return guid != new Guid();
        }

        public bool CheckDateWithinRange(DateTime min, DateTime max, DateTime input)
        {
            return input >= min && input <= max;
        }
    }
}
